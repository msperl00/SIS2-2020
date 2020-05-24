package sis2.pkg2020.controlador;

/**
 * Created by yusufcakmak on 8/3/15.
 */
import java.util.Properties;
import org.hibernate.Session;

import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Configuration;
import org.hibernate.service.ServiceRegistry;

public class HibernateUtil {

    private static StandardServiceRegistry registry;

    //XML based configuration
    private static SessionFactory sessionFactory;

    private static SessionFactory buildSessionFactory() {
        try {
            // Create the SessionFactory from hibernate.cfg.xml
            System.out.println("HOLA");
                    
            Configuration configuration = new Configuration();
            configuration.configure("hibernate.cfg.xml");
            System.out.println("Hibernate Configuration loaded");

            ServiceRegistry serviceRegistry = new StandardServiceRegistryBuilder().applySettings(configuration.getProperties()).build();
            System.out.println("Hibernate serviceRegistry created");

            registry = (StandardServiceRegistry) serviceRegistry;

            SessionFactory sessionFactory = configuration.buildSessionFactory(serviceRegistry);

            return sessionFactory;
        } catch (Throwable ex) {
            // Make sure you log the exception, as it might be swallowed
            System.err.println("Initial SessionFactory creation failed." + ex);
            throw new ExceptionInInitializerError(ex);
        }
    }

    public static SessionFactory getSessionFactory() {
        if (sessionFactory == null) {
            sessionFactory = buildSessionFactory();
        }
        return sessionFactory;
    }

    public static void cerrarSessionFactory() {

        if (!sessionFactory.isClosed()) {
            sessionFactory.close();

        }
    }

    public static void shutdown() {
        if (registry != null) {
            StandardServiceRegistryBuilder.destroy(registry);
        }
    }

    /**
     * Abrimos la conexion con hibernate para trabajar con la sesion
     * directamente
     *
     * @return Session
     */

    public static Session abrirConexionHibernate() {
        //Se abre la sesion de hibernate
        Session session = HibernateUtil.getSessionFactory().openSession();
        //Las transacciones trabajan con la base de datos

        return session;
    }
    /**
     * Abrimos la transaccion con la base de datos, y devolvemos la transaccion.
     * @param session
     * @return 
     */
    public static Transaction abrirConexionBbdd(Session session) {
        Transaction t = session.beginTransaction();
        return t;
    }
    /**
     * Cerramos tanto la sesion de hibernate como la transacion con la base de datos.
     * @param t
     * @param session 
     */
    public static void cerrarConexiones(Transaction t, Session session) {
        t.commit();
        session.close();
        HibernateUtil.cerrarSessionFactory();
        HibernateUtil.shutdown();
    }

}
