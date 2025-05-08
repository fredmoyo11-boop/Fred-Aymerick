package com.sep.backend.account;

import org.springframework.security.access.prepost.PreAuthorize;

import java.lang.annotation.*;
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@PreAuthorize("@accountService.isOwner(#username)")
@Documented
public @interface IsOwner {
}

