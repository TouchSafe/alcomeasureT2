/* JNativeHook: Global keyboard and mouse hooking for Java.
 * Copyright (C) 2006-2018 Alexander Barker.  All Rights Received.
 * https://github.com/kwhat/jnativehook/
 *
 * JNativeHook is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published
 * by the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * JNativeHook is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.github.kwhat.jnativehook;

// Imports.
import org.jnativehook.NativeSystem;
import org.junit.Test;
import static org.junit.Assert.assertFalse;

public class NativeSystemTest {
	/**
	 * Test of getFamily method, of class NativeSystem.
	 */
	@Test
	public void testGetFamily() {
		System.out.println("getFamily");

		assertFalse(NativeSystem.getFamily().equals(NativeSystem.Family.UNSUPPORTED));
	}

	/**
	 * Test of getArchitecture method, of class NativeSystem.
	 */
	@Test
	public void testGetArchitecture() {
		System.out.println("getArchitecture");

		assertFalse(NativeSystem.getArchitecture().equals(NativeSystem.Arch.UNSUPPORTED));
	}
}
