@echo off
 REM Copyright (c) 2013, Equal Experts Ltd
 REM All rights reserved.
 REM
 REM Redistribution and use in source and binary forms, with or without
 REM modification, are permitted provided that the following conditions are
 REM met:
 REM
 REM 1. Redistributions of source code must retain the above copyright notice,
 REM    this list of conditions and the following disclaimer.
 REM 2. Redistributions in binary form must reproduce the above copyright
 REM    notice, this list of conditions and the following disclaimer in the
 REM    documentation and/or other materials provided with the distribution.
 REM
 REM THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 REM "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED
 REM TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A
 REM PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER
 REM OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 REM EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 REM PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 REM PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 REM LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 REM NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 REM SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 REM
 REM The views and conclusions contained in the software and documentation
 REM are those of the authors and should not be interpreted as representing
 REM official policies, either expressed or implied, of the Tayra Project.

if not defined TAYRA_HOME (SET TAYRA_HOME=.)

SET CLASSPATH=%%JAVA_CLASS_PATH%%

java -cp %CLASSPATH% com.ee.tayra.runner.Runner "backup" %*