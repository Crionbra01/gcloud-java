// Generated by the protocol buffer compiler.  DO NOT EDIT!
// source: google/pubsub/v1/pubsub.proto

package com.google.pubsub.v1;

public interface PullResponseOrBuilder extends
    // @@protoc_insertion_point(interface_extends:google.pubsub.v1.PullResponse)
    com.google.protobuf.MessageOrBuilder {

  /**
   * <code>repeated .google.pubsub.v1.ReceivedMessage received_messages = 1;</code>
   *
   * <pre>
   * Received Pub/Sub messages. The Pub/Sub system will return zero messages if
   * there are no more available in the backlog. The Pub/Sub system may return
   * fewer than the maxMessages requested even if there are more messages
   * available in the backlog.
   * </pre>
   */
  java.util.List<com.google.pubsub.v1.ReceivedMessage> 
      getReceivedMessagesList();
  /**
   * <code>repeated .google.pubsub.v1.ReceivedMessage received_messages = 1;</code>
   *
   * <pre>
   * Received Pub/Sub messages. The Pub/Sub system will return zero messages if
   * there are no more available in the backlog. The Pub/Sub system may return
   * fewer than the maxMessages requested even if there are more messages
   * available in the backlog.
   * </pre>
   */
  com.google.pubsub.v1.ReceivedMessage getReceivedMessages(int index);
  /**
   * <code>repeated .google.pubsub.v1.ReceivedMessage received_messages = 1;</code>
   *
   * <pre>
   * Received Pub/Sub messages. The Pub/Sub system will return zero messages if
   * there are no more available in the backlog. The Pub/Sub system may return
   * fewer than the maxMessages requested even if there are more messages
   * available in the backlog.
   * </pre>
   */
  int getReceivedMessagesCount();
  /**
   * <code>repeated .google.pubsub.v1.ReceivedMessage received_messages = 1;</code>
   *
   * <pre>
   * Received Pub/Sub messages. The Pub/Sub system will return zero messages if
   * there are no more available in the backlog. The Pub/Sub system may return
   * fewer than the maxMessages requested even if there are more messages
   * available in the backlog.
   * </pre>
   */
  java.util.List<? extends com.google.pubsub.v1.ReceivedMessageOrBuilder> 
      getReceivedMessagesOrBuilderList();
  /**
   * <code>repeated .google.pubsub.v1.ReceivedMessage received_messages = 1;</code>
   *
   * <pre>
   * Received Pub/Sub messages. The Pub/Sub system will return zero messages if
   * there are no more available in the backlog. The Pub/Sub system may return
   * fewer than the maxMessages requested even if there are more messages
   * available in the backlog.
   * </pre>
   */
  com.google.pubsub.v1.ReceivedMessageOrBuilder getReceivedMessagesOrBuilder(
      int index);
}
