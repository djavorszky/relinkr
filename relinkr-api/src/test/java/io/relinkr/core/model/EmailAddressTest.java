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

package io.relinkr.core.model;

import io.relinkr.core.model.EmailAddress;
import org.junit.Test;

public class EmailAddressTest {

  @Test
  public void givenValidAddressWithNewTld_whenCreate_thenCreated() {
    EmailAddress.of("fabio@disapproved.solutions");
  }

  @Test
  public void givenValidAddressWithOldTld_whenCreate_thenCreated() {
    EmailAddress.of("fabio@disapproved-solutions.com");
  }

  @Test(expected = IllegalArgumentException.class)
  public void givenInvalidAddress_whenCreate_thenIllegalArgumentException() {
    EmailAddress.of("zoé@disapproved-solutions.com");
  }

}
