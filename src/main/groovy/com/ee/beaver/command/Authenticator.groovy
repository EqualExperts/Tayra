package com.ee.beaver.command

interface Authenticator {

	boolean authenticate(String username, String password)

}
