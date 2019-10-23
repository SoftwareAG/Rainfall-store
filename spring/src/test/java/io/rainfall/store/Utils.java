/*
 * Copyright (c) 2014-2019 Aurélien Broszniowski
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.rainfall.store;

import io.rainfall.store.data.Payload;
import io.rainfall.store.values.OutputLog;
import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.io.InputStream;

import static io.rainfall.store.data.CompressionFormat.LZ4;
import static io.rainfall.store.data.CompressionServiceFactory.compressionService;
import static io.rainfall.store.data.Payload.raw;

public class Utils {

  public static Payload readBytes(String fileName) throws IOException {
    try (InputStream is = Utils.class.getResourceAsStream(fileName)) {
      byte[] bytes = IOUtils.toByteArray(is);
      return raw(bytes);
    }
  }

  public static OutputLog toLz4CompressedGetOutput(String hlog) {
    String operation = "GET";
    return toLz4CompressedOutput(hlog, operation);
  }

  public static OutputLog toLz4CompressedOutput(String hlog, String operation) {
    try {
      Payload raw = readBytes(hlog);
      Payload compressed = compressionService(LZ4)
          .compress(raw.getData());
      return OutputLog.builder()
          .operation(operation)
          .format("hlog")
          .payload(compressed)
          .build();
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }
}
