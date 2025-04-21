/*
 * Copyright The OpenTelemetry Authors
 * SPDX-License-Identifier: Apache-2.0
 */

package io.opentelemetry.javaagent.instrumentation.metro;

import com.sun.xml.ws.api.message.Message;
import com.sun.xml.ws.api.message.Packet;
import com.sun.xml.ws.api.server.WSEndpoint;

public class MetroRequest {
  private final Packet packet;
  private final String spanName;

  public MetroRequest(WSEndpoint<?> endpoint, Packet packet) {
    this.packet = packet;
    this.spanName = getSpanName(endpoint, packet);
  }

  public String spanName() {
    return spanName;
  }

  public Packet packet() {
    return packet;
  }
  private static String getOperationNameFrom(Packet packet){
    String operationName = "NO_OPERATION";
    try{
      Message request = packet.getMessage();
      String localport = request.getPayloadLocalPart();
      operationName = localport.replace("Datarequestparam", "");
      if(localport == null){
        String nsUri = request.getPayloadNamespaceURI();
        operationName = nsUri;
      }
      return operationName;
    }catch (Throwable e){
      return "Failed-getPayloadLocalPart";
    }

  }
  private static String getSpanName(WSEndpoint<?> endpoint, Packet packet) {
    String serviceName = "NO_NAME";
    String operationName = "NO_OPERATION";
    try{
      serviceName = endpoint.getServiceName().getLocalPart();
      operationName = packet.getWSDLOperation().getLocalPart();
    }catch(Throwable e){
      operationName = getOperationNameFrom(packet);
    }
    return serviceName + "/" + operationName;
  }
}
