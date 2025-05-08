package com.sep.backend.account;

import io.swagger.v3.oas.annotations.media.Schema;
import org.springframework.security.access.prepost.PreAuthorize;

import java.lang.annotation.*;
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@PreAuthorize("@accountService.isOwner(#username)")
@Documented
@Schema(description = "Checks if the current user is the owner of the account.")
public @interface IsOwner {
}

