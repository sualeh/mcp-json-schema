/*
 * MCP JSON Schema
 * Copyright (c) 2025, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: CC-BY-NC-4.0
 */

package us.fatehi.test;

import java.net.URI;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;

public record FormatTypesParameters(
    URI uriValue,
    LocalDate dateValue,
    OffsetDateTime offsetDateTimeValue,
    java.util.Date utilDateValue,
    LocalDateTime localDateTimeValue,
    URI[] uriArray,
    LocalDate[] dateArray,
    OffsetDateTime[] dateTimeArray) {}
