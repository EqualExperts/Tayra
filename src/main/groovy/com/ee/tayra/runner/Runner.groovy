package com.ee.tayra.runner

import com.ee.tayra.command.*

def scriptName = args[0]
Binding context = new Binding()
context.setVariable('args', args[1..<args.length])
if (scriptName == 'backup') {
  script = new Backup(context)
} else if (scriptName == 'restore') { 
	script = new Restore(context)
} else {
  throw new IllegalArgumentException("Don't know how to process: $scriptName")
}

script.run()
