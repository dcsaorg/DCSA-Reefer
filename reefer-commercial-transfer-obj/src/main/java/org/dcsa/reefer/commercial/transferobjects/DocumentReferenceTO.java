package org.dcsa.reefer.commercial.transferobjects;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import org.dcsa.reefer.commercial.transferobjects.enums.DocumentReferenceType;

public record DocumentReferenceTO(
  @NotNull
  DocumentReferenceType type,

  @NotBlank @Size(max = 100)
  String value
) {
  @Builder(toBuilder = true)
  public DocumentReferenceTO { }
}
