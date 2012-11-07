package com.ee.beaver.runner

import com.ee.beaver.command.*

def scriptName = args[0]
if (scriptName == 'backup') {
  Binding context = new Binding()
  context.setVariable('args', args[1..<args.length])
  script = new Backup(context)
} else if (scriptName == 'restore') { 
	Binding context = new Binding()
	context.setVariable('args', args[1..<args.length])
	script = new Restore(context)
} else {
  throw new IllegalArgumentException("Don't know how to process: $scriptName")
}

script.run()
