package com.abt.safety.model;

import com.abt.common.model.RequestForm;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SafeItemRequestForm extends RequestForm{
   private Boolean itemEnabled;
}
