/*******************************************************************************
 * Copyright (c) 2013, Equal Experts Ltd
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are
 * met:
 *
 * 1. Redistributions of source code must retain the above copyright notice,
 *    this list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in the
 *    documentation and/or other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED
 * TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A
 * PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER
 * OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 * PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *
 * The views and conclusions contained in the software and documentation
 * are those of the authors and should not be interpreted as representing
 * official policies, either expressed or implied, of the Tayra Project.
 ******************************************************************************/
package com.ee.tayra.command

import com.ee.tayra.io.ConsoleReplayer
import com.ee.tayra.io.EmptyProgressReporter
import com.ee.tayra.io.SelectiveOplogReplayer
import java.io.PrintWriter;

class DryRunFactory extends RestoreFactory{

	private final PrintWriter console
	private final def listeningReporter	private final def criteria

	public DryRunFactory(Config config) {
		this.criteria = config.criteria
		this.console = config.console
		listeningReporter = new EmptyProgressReporter()
	}

	@Override
	public def createWriter() {
		new SelectiveOplogReplayer(criteria, new ConsoleReplayer(console))
	}

	@Override
	public def createListener() {
		listeningReporter
	}

	@Override
	public def createReporter() {
		listeningReporter
	}
}
