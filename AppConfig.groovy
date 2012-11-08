def toCompleteVersion() {
	def completeVersion = new StringBuilder()
	completeVersion << configuration.product.version.major
	completeVersion << '.'
	completeVersion << configuration.product.version.minor
	completeVersion << '.'
	completeVersion << configuration.product.version.micro
	completeVersion << '.'
	completeVersion << configuration.product.version.qualifier.alphaNumeric
	completeVersion.toString()
}

configuration {
	product {
		name = 'MongoBeaver'
		
		vendor {
			name = 'Equal Experts Pvt. Ltd.'
		}
        //JBoss Versioning Convention
		version {
			major = 0 //number related to production release
			minor = 2 //changes or feature additions
			micro = 0 //patches and bug fixes
			qualifier {
				alphaNumeric = 'Alpha1' //Alpha# or Beta# or CR# or GA, or SP#
			}
			complete = toCompleteVersion()
		}
		
		distribution {
			name = product.name + '-' + toCompleteVersion()
		
			jar {
				name = product.name
				manifest {
					details = [
						'Manifest-Version' : '1.0',
						'Sealed' : 'true',
						'Specification-Title' : product.name,
						'Specification-Version': toCompleteVersion(),
						'Specification-Vendor':  product.vendor.name,
						'Implementation-Version': toCompleteVersion(),
						'Implementation-Vendor': product.vendor.name
					]
				}
			}
		}
	}
}
