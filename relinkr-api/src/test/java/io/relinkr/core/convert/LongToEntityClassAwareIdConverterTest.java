/*
  Copyright [2018-2019] Laszlo Csontos (sole trader)

  Licensed under the Apache License, Version 2.0 (the "License");
  you may not use this file except in compliance with the License.
  You may obtain a copy of the License at

     http://www.apache.org/licenses/LICENSE-2.0

  Unless required by applicable law or agreed to in writing, software
  distributed under the License is distributed on an "AS IS" BASIS,
  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
  See the License for the specific language governing permissions and
  limitations under the License.
*/

package io.relinkr.core.convert;

import io.relinkr.click.model.ClickId;
import io.relinkr.core.orm.EntityClassAwareId;
import org.junit.Test;
import org.springframework.core.convert.converter.Converter;

public class LongToEntityClassAwareIdConverterTest<T extends EntityClassAwareId<?>>
    extends AbstractEntityClassAwareIdConverterTest<Long, T> {

  @Test(expected = IllegalArgumentException.class)
  public void givenWrongClass_whenCreate_thenIllegalArgumentException() {
    new LongToEntityClassAwareIdConverter(Object.class);
  }

  @Override
  protected Converter<Long, T> createConverter() {
    return new LongToEntityClassAwareIdConverter(ClickId.class);
  }

  @Override
  protected Long getGoodValue() {
    return 1L;
  }

}
