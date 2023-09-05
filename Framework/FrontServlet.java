package etu2070.framework.servlet;

import java.io.*;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.sql.Timestamp;
import java.util.Date;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ArrayList;
import java.util.Map.Entry;
import etu2070.framework.ModelView;
import javax.servlet.annotation.MultipartConfig;
import etu2070.annotation.url;
import etu2070.annotation.csv;
import etu2070.annotation.Scope;
import etu2070.annotation.Setter;
import etu2070.annotation.auth;
import etu2070.annotation.restAPI;
import etu2070.annotation.session;
import etu2070.framework.FileUpload;
import etu2070.framework.Mapping;
import javax.servlet.*;
import javax.servlet.http.*;
import java.util.Collection;
import java.util.Enumeration;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import connection.BddObject;
import connection.Column;

@MultipartConfig
public class FrontServlet extends HttpServlet {

    Map<String, Mapping> mappingUrls;
    Map<String, Object> singletons;

    private static final Map<String, Class<?>> wrapperClassMap;
    static {
        wrapperClassMap = new HashMap<>();
        // Add mappings for primitive types and their wrapper classes
        wrapperClassMap.put("int", Integer.class);
        wrapperClassMap.put("char", Character.class);
        wrapperClassMap.put("boolean", Boolean.class);
        wrapperClassMap.put("byte", Byte.class);
        wrapperClassMap.put("short", Short.class);
        wrapperClassMap.put("long", Long.class);
        wrapperClassMap.put("float", Float.class);
        wrapperClassMap.put("double", Double.class);
        wrapperClassMap.put("String", String.class);
    }

    public void init() throws ServletException {
        try {
            this.mappingUrls = new HashMap<>();
            this.singletons = new HashMap<>();
            String packageName = this.getServletConfig().getInitParameter("directory");
            URL root = Thread.currentThread().getContextClassLoader().getResource(packageName.replace(".", "/"));
            File file = new File(root.getFile().replace("%20", " "));
            this.initMapping(file.listFiles(), packageName);
        } catch (Exception e) {
            throw new ServletException(e);
        }
    }

    public void initMapping(File[] files, String packageName) throws Exception {
        for (File file : files) {
            if (!file.isDirectory()) {
                String className = file.getName().replaceAll(".class$", "");
                Class<?> cls = Class.forName(packageName + "." + className);
                if (cls.isAnnotationPresent(Scope.class) && cls.getAnnotation(Scope.class).value().equals("singleton")) {
                    this.singletons.put(cls.getName(), null);
                }
                for (Method method : cls.getDeclaredMethods()) {
                    if (method.isAnnotationPresent(url.class)) {
                        this.mappingUrls.put(method.getAnnotation(url.class).value(), new Mapping(cls.getName(), method.getName()));
                    }
                }
            } else {
                this.initMapping(file.listFiles(), packageName + "." + file.getName());
            }
        }
    }

    public void processRequest(HttpServletRequest request, HttpServletResponse response)
        throws IOException, ServletException {
        try {
            String url = request.getRequestURI().substring(request.getContextPath().length() + 1); // Get URL
            
            // If url not found, throw exception
            if (!this.mappingUrls.containsKey(url)) throw new ServletException("URL \"" + url + "\" not found");
            
            // Get class and method with reflection
            Mapping mapping = mappingUrls.get(url);
            Class<?> cls = Class.forName(mapping.getClassName()); // Class of this mapping
            
            Object obj = this.getObjectSingleton(cls); // Get the singleton in HashMap

            // Set parameters to object
            Method method = mapping.getDeclaredMethod();
            Object methodValue = null;

            // Adding Session in controller
            if (method.isAnnotationPresent(session.class)) {
                HttpSession session = request.getSession();
                Enumeration<String> attributes = request.getSession().getAttributeNames();
                Map<String, Object> mapSession = new HashMap<>();
                while (attributes.hasMoreElements()) {
                    String attribute = attributes.nextElement();
                    mapSession.put(attribute, request.getSession().getAttribute(attribute));
                }
                cls.getDeclaredMethod("setSession", HashMap.class).invoke(obj, mapSession);
            }
            
            try {
                // Check authentification in method
                if (method.isAnnotationPresent(auth.class)) {
                    HttpSession session = request.getSession();
                    Object value = session.getAttribute(this.getServletConfig().getInitParameter("sessionName"));
                    if (value == null) throw new Exception("Authentification required");
                    String profil = (String) session.getAttribute(this.getServletConfig().getInitParameter("sessionProfil"));
                    if (!method.getAnnotation(auth.class).value().isEmpty()) {
                        if (profil == null) throw new Exception("Authentification profil is required");
                        if (!profil.equals(method.getAnnotation(auth.class).value())) throw new Exception("Authentification " + method.getAnnotation(auth.class).value() + " is required");
                    }
                }

                // Set data in object
                for (Field field : this.getAllFields(obj)) {
                    // Initialiser les attibuts de l'objet
                    if (!field.getType().isPrimitive() && cls.isAnnotationPresent(Scope.class)) {
                        field.setAccessible(true);
                        field.set(obj, null);
                    }

                    Class<?> type = (field.isAnnotationPresent(Setter.class)) ? field.getAnnotation(Setter.class).value() : field.getType();
                    Object value = (type.isAssignableFrom(FileUpload.class)) ? toFileUpload(request, field.getName()) : cast(type, request.getParameter(field.getName()));
                    if (value != null) {
                        Method setter = cls.getMethod("set" + field.getName().substring(0, 1).toUpperCase() + field.getName().substring(1), type);
                        setter.invoke(obj, value);
                    }
                }
    
                // If the method have parameters set them in each paramaters
                Parameter[] parameters = method.getParameters();
                Object[] values = new Object[parameters.length];
                for (int i = 0; i < parameters.length; i++) {
                    values[i] = cast(parameters[i].getType(), request.getParameter(parameters[i].getName()));
                }
             
                // Execute controller Method
                methodValue = method.invoke(obj, values);

            } catch (Exception e) {
                String urlError = method.getAnnotation(url.class).error();
                if (urlError.isEmpty()) throw e;
                mapping = this.mappingUrls.get(urlError);
                cls = Class.forName(mapping.getClassName());
                obj = this.getObjectSingleton(cls);
                // Execute controller Method for manage Exception
                methodValue = mapping.getDeclaredMethod().invoke(obj, e);
            
            }

            PrintWriter out = response.getWriter();
            // If method return ModelView, we forward to the view
            if (methodValue instanceof ModelView) {
                ModelView view = (ModelView) methodValue;
                
                // Add Data in .jsp with dispatcher
                for (Entry<String, Object> map : view.getData().entrySet()) {
                    request.setAttribute(map.getKey(), map.getValue());
                }

                // Add Session
                for (Entry<String, Object> map : view.getSession().entrySet()) {
                    request.getSession().setAttribute(map.getKey(), map.getValue());
                }

                // Remove Session
                for (String session : view.getRemove()) {
                    request.getSession().removeAttribute(session);
                }

                // Invalidate Sesssion
                if (view.isInvalidate()) request.getSession().invalidate();

                // Rest API return json
                if (view.isJson()) {
                    out.println(createGson().toJson(view.getData()));
                } else {
                    if (view.getView() != null) {
                        request.getRequestDispatcher("/" + view.getView()).forward(request, response);
                    } else  {
                        response.sendRedirect(view.getRedirect());
                    }
                }
            } else {
                out.println((method.isAnnotationPresent(restAPI.class)) ? createGson().toJson(methodValue) : (method.isAnnotationPresent(csv.class)) ? convertToCsv((Object[]) methodValue, method) : methodValue.toString());
            }
        } catch(Exception e) {
            throw new ServletException(e);
        }
    }

    public Object getObjectSingleton(Class<?> cls) throws Exception {
        Object obj = this.singletons.get(cls.getName()); // Get the singleton in HashMap
        if (obj == null) {
            obj = cls.getConstructor().newInstance(); // Create new instance if singleton is null
            // replace the null value if the class is Singleton
            if (cls.isAnnotationPresent(Scope.class)) {
                this.singletons.replace(cls.getName(), obj);
            }
        }
        return obj;
    }

    public static Gson createGson() {
        GsonBuilder builder = new GsonBuilder(); 
        builder.setPrettyPrinting();
        return builder.create();
    }

    public String convertToCsv(Object[] values, Method method) throws Exception {
        String csv = "";
        for (String colonne : method.getAnnotation(csv.class).value()) csv += colonne + ",";
        if (csv.length() > 0)
            csv = csv.substring(0, csv.length() - 1) + "\n";
        for (Object object : values) {
            csv += (String) object.getClass().getDeclaredMethod("toCsv").invoke(object) + "\n";
        }
        return csv;
    }

    public FileUpload toFileUpload(HttpServletRequest request, String name) throws Exception {
        String contentType = request.getContentType();
        if (contentType == null || !contentType.startsWith("multipart/")) return null;
        Part part = request.getPart(name);
        InputStream input = part.getInputStream();
        byte[] b = new byte[input.available()];
        input.read(b);
        String filename = getFileName(part);
        return new FileUpload(filename, b);
    }

    public String getFileName(Part part) throws Exception {
        String contentDisposition = part.getHeader("content-disposition");
        String[] parts = contentDisposition.split(";");
        for (String partStr : parts) {
            if (partStr.trim().startsWith("filename")) return partStr.substring(partStr.indexOf('=') + 1).trim().replace("\"", "");
        }
        return null;
    }

    public Object convertToDate(String dateFormat, Class<?> type) throws Exception {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        java.util.Date date = sdf.parse(dateFormat);
        return (type.isAssignableFrom(java.util.Date.class)) ? date 
        : (type.isAssignableFrom(java.sql.Date.class)) ? new java.sql.Date(date.getTime()) : null;
    }

    public Object convertToTimestamp(String dateFormat) throws Exception {
        dateFormat = dateFormat.replace("T", ",");
        DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd,HH:mm");
        Date date = formatter.parse(dateFormat);
        return new Timestamp(date.getTime());
    }

    public Object cast(Class<?> type, String value) throws Exception {
        return (value == null) ? null 
                : (type.getSimpleName().equals("Date")) ? convertToDate(value, type) 
                : (type.getSimpleName().equals("Timestamp")) ? convertToTimestamp(value)
                : (isBddObjectType(type)) ? convertToBddObject(type, value)
                : wrapperClassMap.get(type.getSimpleName()).getConstructor(String.class).newInstance(value);
    }

    public void doPost(HttpServletRequest request, HttpServletResponse response)
        throws IOException, ServletException {
        processRequest(request, response);
    }

    public void doGet(HttpServletRequest request, HttpServletResponse response)
        throws IOException, ServletException {
        processRequest(request, response);
    }

    public List<Field> getAllFields(Object obj) throws Exception {
        Class<?> c = obj.getClass();
        List<Field> columns = new ArrayList<>();
        while (c != null) {
            for (Field field : c.getDeclaredFields()) columns.add(field);
            c = c.getSuperclass();
        }
        return columns;
    }

    public static boolean isBddObjectType(Class<?> c) {
        return BddObject.class.isAssignableFrom(c);
    }

    public BddObject convertToBddObject(Class<?> type, String value) throws Exception {
        BddObject object = (BddObject) type.getConstructor().newInstance();
        Column primaryKey = object.getFieldPrimaryKey();
        Method setter = object.getClass().getMethod("set" + primaryKey.getField().getName().substring(0, 1).toUpperCase()  + primaryKey.getField().getName().substring(1), primaryKey.getField().getType());
        setter.invoke(object, value);
        return object;
    }
    
}