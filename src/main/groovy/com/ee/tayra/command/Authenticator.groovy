package com.ee.tayra.command

interface Authenticator {

	boolean authenticate(String username, String password)

}
