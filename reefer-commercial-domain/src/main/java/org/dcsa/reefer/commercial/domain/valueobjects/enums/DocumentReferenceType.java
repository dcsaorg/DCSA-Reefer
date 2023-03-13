package org.dcsa.reefer.commercial.domain.valueobjects.enums;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public enum DocumentReferenceType {
  CBR("Carrier Booking Request"),
  BKG("Booking"),
  SHI("Shipping Instruction"),
  TRD("Transport Document"),
  DEI("Delivery Instructions"),
  DEO("Delivery Order"),
  TRO("Transport Order"),
  CRO("Container Release Order"),
  ARN("Arrival Notice"),
  VGM("Verified Gross Mass"),
  CAS("Cargo Survey"),
  CUC("Customs Clearance"),
  DGD("Dangerous Goods Declaration"),
  OOG("Out of Gauge"),
  CQU("Contract Quotation"),
  INV("Invoice"),
  HCE("Health Certificate"),
  PCE("Phytosanitary Certificate"),
  VCE("Veterinary Certificate"),
  FCE("Fumigation Certificate"),
  ICE("Inspection Certificate"),
  CEA("Certificate of Analysis"),
  CEO("Certificate of Origin")
  ;

  @Getter
  private final String description;
}
