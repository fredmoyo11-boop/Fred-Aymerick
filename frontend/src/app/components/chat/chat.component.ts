import {Component, inject, Input, OnDestroy, OnInit, ViewChild} from '@angular/core';
import {
  ChatService,
  InboundChatAction,
  OutboundChatMessage, TripOffer,
} from '../../../api/sep_drive';
import {StompService} from '../../services/stomp.service';
import {Subscription} from 'rxjs';
import {MatCardModule} from '@angular/material/card';
import {MatIcon} from '@angular/material/icon';
import {NgClass} from '@angular/common';
import {MatDivider} from '@angular/material/divider';
import {MatFormField, MatInput, MatLabel, MatSuffix} from '@angular/material/input';
import {MatIconButton} from '@angular/material/button';
import {FormControl, FormGroup, ReactiveFormsModule, Validators} from '@angular/forms';
import {AngularAuthService} from '../../services/angular-auth.service';

@Component({
  selector: 'app-chat',
  imports: [
    MatCardModule,
    MatIcon,
    NgClass,
    MatDivider,
    MatFormField,
    MatIconButton,
    MatLabel,
    ReactiveFormsModule,
    MatInput,
    MatSuffix,
  ],
  templateUrl: './chat.component.html',
  styleUrl: './chat.component.css'
})
export class ChatComponent implements OnInit, OnDestroy {
  @Input() tripOffer!: TripOffer
  @Input() tripOfferId!: string

  private stompService = inject(StompService)
  private chatService = inject(ChatService)
  private angularAuthService = inject(AngularAuthService)

  private role!: string
  private subscription!: Subscription;

  chatForm = new FormGroup({
    message: new FormControl("", [Validators.required, Validators.minLength(1), Validators.maxLength(10000)])
  })

  chatMessages: OutboundChatMessage[] = [];
  editMessageId: number | null = null

  @ViewChild("scrollContainer") private scrollContainer!: HTMLElement;


  isOwnMessage(chatMessage: OutboundChatMessage): boolean {
    return (this.role === "DRIVER" && chatMessage.direction === "D2C") || (this.role === "CUSTOMER" && chatMessage.direction === "C2D")
  }

  getPartnerNames() {
    if (this.role === "CUSTOMER") {
      return {
        firstName: this.tripOffer.driver.firstName,
        lastName: this.tripOffer.driver.lastName,
        username: this.tripOffer.driver.username
      }
    } else {
      return {
        firstName: this.tripOffer.tripRequest.customer.firstName,
        lastName: this.tripOffer.tripRequest.customer.lastName,
        username: this.tripOffer.tripRequest.customer.username
      }
    }
  }

  ngOnInit(): void {
    this.angularAuthService.role$.subscribe({
      next: value => {
        if (value) {
          console.log("Role changed to", value)
          this.role = value
        }
      },
      error: err => {
        console.error(err);
      }
    })

    this.chatService.getAllMessages(Number(this.tripOfferId)).subscribe({
      next: value => {
        console.log("Value", value)
        if (value) {
          this.chatMessages = value;
          // mark messages as seen after "opening" chat
          this.chatMessages.forEach(message => {
            console.log(message);
            if (!message.seen && ((this.role === "CUSTOMER" && message.direction === "D2C") || (this.role === "DRIVER" && message.direction === "C2D"))) {
              console.log("Marking as seen", message);
              this.seen(message.chatMessageId)
            }
          })
        }
      },
      error: err => {
        console.error(err);
      }
    })

    this.subscription = this.stompService
      .watchTopic<OutboundChatMessage>(`/topic/trip/${this.tripOfferId}`)
      .subscribe(message => {
        console.log("Received from socket:", message)
        this.chatMessages = this.upsertMessage(this.chatMessages, message)
      })
  }

  ngOnDestroy(): void {
    this.subscription.unsubscribe();
  }

  onSubmit(): void {
    if (this.editMessageId != null) {
      this.edit(this.chatForm.value.message!, Number(this.editMessageId))
    } else {
      this.send(this.chatForm.value.message!)
    }
    this.chatForm.reset()
  }

  setEditMessageId(chatMessageId: number): void {
    this.editMessageId = chatMessageId
    this.chatForm.setValue({message: this.chatMessages.find(m => m.chatMessageId === Number(chatMessageId))?.content ?? ""})
  }

  resetEditMessageId(): void {
    this.editMessageId = null
    this.chatForm.reset()
  }

  send(content: string): void {
    const action: InboundChatAction = {actionType: "SEND", content}
    this.stompService.send(`/app/chat/${this.tripOfferId}`, action)
  }

  seen(chatMessageId: number): void {
    const action: InboundChatAction = {actionType: "SEEN", chatMessageId}
    this.stompService.send(`/app/chat/${this.tripOfferId}`, action)
  }

  edit(content: string, chatMessageId: number): void {
    this.editMessageId = null
    const action: InboundChatAction = {actionType: "EDIT", content, chatMessageId}
    this.stompService.send(`/app/chat/${this.tripOfferId}`, action)
  }

  delete(chatMessageId: number): void {
    const action: InboundChatAction = {actionType: "DELETE", chatMessageId}
    this.stompService.send(`/app/chat/${this.tripOfferId}`, action)
  }

  upsertMessage = (messages: OutboundChatMessage[], message: OutboundChatMessage): OutboundChatMessage[] => {
    const index = messages.findIndex(m => m.chatMessageId === message.chatMessageId)
    if (index === -1) {
      // mark new messages as seen, if we are receiving we are in the chat active
      if ((this.role === "CUSTOMER" && message.direction === "D2C") || (this.role === "DRIVER" && message.direction === "C2D")) {
        this.seen(message.chatMessageId)
      }
      return [...messages, message]
    } else {
      return [
        ...messages.slice(0, index),
        message,
        ...messages.slice(index + 1)
      ]
    }
  }


}
