/*
 * MCP JSON Schema
 * Copyright (c) 2025, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: CC-BY-NC-4.0
 */

package us.fatehi.test;

// No annotations by requirement
public record AllTypesParameters(
    int integerValue,
    double numberValue,
    String stringValue,
    boolean booleanValue,
    MyEnum enumValue,
    int[] integerArray,
    MyEnum[] enumArray) {

  public enum MyEnum {
    A,
    B,
    C;
  }
}
