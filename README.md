# Documentation du Framework
1. Setup du framwork

    - Mettre le fichier .jar dans le lib de votre projet

    - Instancier le FrontServlet dans web.xml
    ```
    <servlet>
        <servlet-name>front-servlet</servlet-name>
        <servlet-class>etu2070.framework.servlet.FrontServlet</servlet-class>
            <init-param>
            <param-name>directory</param-name>
            <param-value>Name of directory</param-value>
        </init-param>
    </servlet>
    <servlet-mapping>
        <servlet-name>front-servlet</servlet-name>
        <url-pattern>/</url-pattern>
    </servlet-mapping>
    ```