/*
 * MCP JSON Schema
 * Copyright (c) 2025, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: CC-BY-NC-4.0
 */

package us.fatehi.test;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record StringLengthParameters(
    @Size(min = 2) String minValue,
    @Size(max = 3) String maxValue,
    @Size(min = 2, max = 3) String lenValue,
    @NotBlank String notBlankValue) {}
