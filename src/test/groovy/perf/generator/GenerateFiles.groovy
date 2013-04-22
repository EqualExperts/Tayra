package perf.generator

fileSizes = [
	new Tuple(1, 'mb'),
	new Tuple(2, 'mb'),
	new Tuple(4, 'mb'),
	new Tuple(8, 'mb'),
	new Tuple(16, 'mb'),
	new Tuple(32, 'mb'),
	new Tuple(64, 'mb'),
	new Tuple(128, 'mb'),
	new Tuple(256, 'mb'),
	new Tuple(512, 'mb'),
	new Tuple(1, 'gb'),
	new Tuple(2, 'gb'),
	new Tuple(4, 'gb')
]

fileSizes.each { tuple ->
	def fileSize = tuple[0]
	def unit = tuple[1]
	def binding = new Binding()
	def args = ['-f', "test.$fileSize$unit", '-s', "$fileSize", '-u', "$unit"]
	binding.setVariable('args', args)
	new Generate(binding).run()
}
