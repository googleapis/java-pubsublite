/*
 * Copyright 2020 Google LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.google.cloud.pubsublite.kafka;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableListMultimap;
import com.google.common.collect.Iterables;
import com.google.common.collect.Iterators;
import com.google.protobuf.ByteString;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import org.apache.kafka.common.header.Header;
import org.apache.kafka.common.header.Headers;

class LiteHeaders implements Headers {
  private ImmutableListMultimap<String, ByteString> attributes;

  LiteHeaders(ImmutableListMultimap<String, ByteString> attributes) {
    this.attributes = attributes;
  }

  static Header toHeader(String key, ByteString value) {
    return new Header() {
      @Override
      public String key() {
        return key;
      }

      @Override
      public byte[] value() {
        return value.toByteArray();
      }
    };
  }

  private static List<Header> toHeaders(String key, Collection<ByteString> values) {
    ImmutableList.Builder<Header> headersBuilder = ImmutableList.builder();
    values.forEach(value -> headersBuilder.add(toHeader(key, value)));
    return headersBuilder.build();
  }

  @Override
  public Headers add(Header header) throws IllegalStateException {
    throw new IllegalStateException();
  }

  @Override
  public Headers add(String s, byte[] bytes) throws IllegalStateException {
    throw new IllegalStateException();
  }

  @Override
  public Headers remove(String s) throws IllegalStateException {
    throw new IllegalStateException();
  }

  @Override
  public Header lastHeader(String s) {
    return Iterables.getLast(this);
  }

  @Override
  public Iterable<Header> headers(String s) {
    if (attributes.containsKey(s))
      return Iterables.transform(attributes.get(s), value -> toHeader(s, value));
    return ImmutableList.of();
  }

  @Override
  public Header[] toArray() {
    ImmutableList.Builder<Header> arrayBuilder = ImmutableList.builder();
    attributes
        .entries()
        .forEach(entry -> arrayBuilder.add(toHeader(entry.getKey(), entry.getValue())));
    return (Header[]) arrayBuilder.build().toArray();
  }

  @Override
  public Iterator<Header> iterator() {
    return Iterators.transform(
        attributes.entries().iterator(), entry -> toHeader(entry.getKey(), entry.getValue()));
  }
}
