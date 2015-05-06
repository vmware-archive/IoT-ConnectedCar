/*
 * Copyright 2014-2015 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.acmemotors.domain;

/**
 * @author mminella
 */
public enum OBDVersion {
	OBD_II,
	OBD,
	OBD_AND_OBD_II,
	OBD_I,
	NOT_OBD,
	EOBD,
	EOBD_AND_OBD_II,
	EOBD_AND_OBD,
	EOBD_OBD_AND_OBD_II,
	JOBD,
	JOBD_AND_OBD_II,
	JOBD_AND_EOBD,
	JOBD_EOBD_AND_OBD_II,
	EMD,
	EMD_PLUS,
	HD_OBD_C,
	HD_OBD,
	WWH_OBD,
	HD_EOBD_I,
	HD_EOBD_I_N,
	HD_EOBD_II,
	HD_EOBD_II_N,
	OBDBr_1,
	OBDBr_2,
	KOBD,
	IOBD_I,
	IOBD_II,
	HD_EOBD_IV;
}
