package me.config.impl;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import me.config.api.IChangeListener;
import me.config.api.IChangeableConfig;
import me.config.api.IConfig;
import me.config.base.ChangeableConfig;
import org.junit.Test;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

/**
 * 测试多个配置合并
 * Created by lirui on 2015-09-28 19:21.
 */
public class MergedConfigTest {
  @Test
  public void testMerge() throws Exception {
    List<IChangeableConfig> raw = Lists.newArrayList();
    ChangeableConfig first = new ChangeableConfig("first");
    first.copyOf(ImmutableMap.of("mix", "m1", "k1", "v1"));
    raw.add(first);
    ChangeableConfig second = new ChangeableConfig("second");
    second.copyOf(ImmutableMap.of("mix", "m2", "k2", "v2"));
    raw.add(second);
    MergedConfig merged = new MergedConfig(raw);
    assertThat(merged.getName(), is("first,second"));
    //验证排在前面的优先
    assertThat(merged.get("mix"), is("m1"));
    assertThat(merged.get("k1"), is("v1"));
    assertThat(merged.get("k2"), is("v2"));

    final AtomicInteger count = new AtomicInteger(0);
    merged.addListener(new IChangeListener() {
      @Override
      public void changed(IConfig config) {
        count.incrementAndGet();
      }
    });

    assertThat(count.get(), is(1));
    //第一个文件变更通知
    first.notifyListeners();
    assertThat(count.get(), is(2));
    //第二个文件变更通知
    second.notifyListeners();
    assertThat(count.get(), is(3));
  }
}