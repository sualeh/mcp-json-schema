/*
 * MCP JSON Schema
 * Copyright (c) 2025, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: CC-BY-NC-4.0
 */

package us.fatehi.test;

import jakarta.validation.constraints.*;

// No annotations by requirement
public record MinMaxParameters(
    @Min(2) int minIntegerValue,
    @Min(2) double minNumberValue,
    @Max(3) int maxIntegerValue,
    @Min(3) double maxNumberValue,
    @Positive int posIntegerValue,
    @Positive double posNumberValue,
    @PositiveOrZero int pos0IntegerValue,
    @PositiveOrZero double pos0NumberValue,
    @Negative int negIntegerValue,
    @Negative double negNumberValue,
    @NegativeOrZero int neg0IntegerValue,
    @NegativeOrZero double neg0NumberValue) {}
