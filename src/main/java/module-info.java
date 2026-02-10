/*
 * MCP JSON Schema
 * Copyright (c) 2025-2026, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: CC-BY-NC-4.0
 */

/**
 * MCP JSON Schema module.
 *
 * <p>This module provides utilities for generating JSON schemas for the Model Context Protocol
 * (MCP) and for deserializing arguments.
 */
module us.fatehi.mcp_json_schema {
  // Jackson dependencies for JSON processing
  requires tools.jackson.databind;
  requires tools.jackson.core;

  // Jakarta validation for constraint annotations
  requires jakarta.validation;

  // Java logging
  requires java.logging;

  // Export only the utility package
  exports us.fatehi.mcp_json_schema.utility;
}
