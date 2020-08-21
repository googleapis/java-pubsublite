package com.google.cloud.pubsublite.internal.wire;

import static com.google.common.truth.Truth.assertThat;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

@RunWith(JUnit4.class)
public class VersionsTest {
  @Test
  public void badSplits() {
    Versions versions = new Versions("1.1");
    assertThat(versions.getMajorVersion()).isEqualTo(0);
    assertThat(versions.getMinorVersion()).isEqualTo(0);
  }

  @Test
  public void garbageMajorVersion() {
    Versions versions = new Versions("abc.1.1");
    assertThat(versions.getMajorVersion()).isEqualTo(0);
    assertThat(versions.getMinorVersion()).isEqualTo(1);
  }

  @Test
  public void garbageMinorVersion() {
    Versions versions = new Versions("1.abc.1");
    assertThat(versions.getMajorVersion()).isEqualTo(1);
    assertThat(versions.getMinorVersion()).isEqualTo(0);
  }
}
