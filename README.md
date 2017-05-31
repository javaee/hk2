

# About

{{site.title}} is a simple demo site. Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor ut labore et dolore aliqua. Ut enim ad minim veniam, quis exercitation ullamco laboris nisi ut aliquip.


Lorem ipsum dolor sit amet again! Consectetur adipiscing elit, sed do eiusmod tempor ut labore et dolore aliqua. Ut enim ad minim veniam, quis exercitation ullamco laboris nisi.
  
## Somes tables

| File  | Description |
| :---: | :--- |
| [javax.mail.jar](https://github.com/javaee/javamail/releases/download/JAVAMAIL-1_5_6/javax.mail.jar)  | The JavaMail reference implementation, including the SMTP, IMAP, and POP3 protocol providers  |
| [README.txt](https://bshannon.github.io/test/docs/README.txt) | Overview of the release |
| [NOTES.txt](https://bshannon.github.io/test/docs/NOTES.txt)	|Additional notes about using JavaMail  |
| [SSLNOTES.txt](https://bshannon.github.io/test/docs/SSLNOTES.txt)	|Notes on using SSL/TLS with JavaMail  |

|jar file|gropuId|artifactId|Description|
| :---: | :---: |  :---: | :--- | 
| javax.mail.jar | com.sun.mail | javax.mail | The JavaMail reference implementation jar file, including the SMTP, IMAP, and POP3 protocol providers |
| javax.mail-api.jar | javax.mail | javax.mail-api | The JavaMail API definitions only, suitable for compiling against |
| mailapi.jar | com.sun.mail | mailapi | The JavaMail reference implementation with no protocol providers; use with one of the following providers |
| smtp.jar | com.sun.mail | smtp | The SMTP protocol provider |

# Some examples

* Fix the bug
* Test the fix
* Push my commits to GitHub
  
1. France  
1.1 Paris  
2.1 Nice  
2. USA  
2.1 New York  
2.2 San Francisco 

> This is a multi line block quote  
and this is the end. 


## April 10th, 2017 - JAX-RS PATCH support (client API) ##

PATCH support has been added to JAX-RS API 2.1 in milestone 6, see [here](https://java.net/projects/jax-rs-spec/lists/users/archive/2017-04/message/40).
Check the [contribute](contribute) page which is a full page.

```java
/**
 * Indicates that the annotated method responds to HTTP PATCH requests.
 *
 * @author Pavel Bucek (pavel.bucek at oracle.com)
 * @see HttpMethod
 * @since 2.1
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@HttpMethod(HttpMethod.GET)
@Documented
public @interface PATCH {
}
```
