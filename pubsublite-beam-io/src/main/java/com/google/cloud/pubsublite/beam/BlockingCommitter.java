package com.google.cloud.pubsublite.beam;

import com.google.cloud.pubsublite.Offset;

interface BlockingCommitter {
  void commit(Offset offset) throws Exception;
}
