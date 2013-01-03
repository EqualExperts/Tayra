package com.ee.beaver.io

class RotatingFileCollection {

	private files

	public RotatingFileCollection(final String regex, final boolean isMultiple) {
		files = getAllRotatingFiles(regex, isMultiple)
	}

	def withFile(Closure closure) {
		files.each { closure(it) }
	}

	private def findFilesInDirectory(String fileNameRegex, File directory) {
		directory.listFiles().findAll{!it.isDirectory() && it.name.startsWith(fileNameRegex) }
	}

	private def getAllRotatingFiles(String fileNameRegex, boolean isMultiple) throws Exception {
		final File directory = new File(System.getProperty("user.dir") + File.separator)
		files = (isMultiple ? findFilesInDirectory(fileNameRegex, directory)
			: [fileNameRegex])
		files.sort { a, b -> b.compareTo(a) }

	}

}
