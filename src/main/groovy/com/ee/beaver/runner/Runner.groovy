package com.ee.beaver.runner

import com.ee.beaver.command.Backup

def context = new Binding()
context.setVariable("args", args)
new Backup(context).run();