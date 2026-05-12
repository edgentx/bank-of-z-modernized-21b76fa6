package com.example.api.tellersession.dto;

import com.example.domain.tellersession.model.NavigateMenuCmd;
import jakarta.validation.constraints.NotBlank;

public record NavigateMenuRequest(
    @NotBlank String menuId,
    @NotBlank String action
) {
  public NavigateMenuCmd toCommand(String sessionId) {
    return new NavigateMenuCmd(sessionId, menuId, action);
  }
}
