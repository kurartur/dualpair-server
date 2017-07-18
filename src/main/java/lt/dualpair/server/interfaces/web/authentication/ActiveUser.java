package lt.dualpair.server.interfaces.web.authentication;

import java.lang.annotation.*;

@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface ActiveUser {}
