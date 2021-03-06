/*
The MIT License (MIT)

Copyright (c) 2015 Los Andes University

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.
*/
package co.edu.uniandes.csw.artwork.tests.rest;

import co.edu.uniandes.csw.auth.model.UserDTO;
import co.edu.uniandes.csw.auth.security.JWT;
import co.edu.uniandes.csw.artwork.entities.ArtworkEntity;
import co.edu.uniandes.csw.artwork.entities.ArtistEntity;
import co.edu.uniandes.csw.artwork.entities.CategoryEntity;
import co.edu.uniandes.csw.artwork.dtos.minimum.CategoryDTO;
import co.edu.uniandes.csw.artwork.resources.ArtworkResource;
import co.edu.uniandes.csw.artwork.tests.Utils;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import javax.inject.Inject;
import javax.transaction.UserTransaction;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Cookie;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import org.codehaus.jackson.map.ObjectMapper;
import org.eclipse.jetty.util.log.Log;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.test.api.ArquillianResource;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.jboss.shrinkwrap.resolver.api.maven.Maven;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import uk.co.jemos.podam.api.PodamFactory;
import uk.co.jemos.podam.api.PodamFactoryImpl;

/*
 * Testing URI: artists/{artworksId: \\d+}/artworks/
 */
@RunWith(Arquillian.class)
public class ArtworkCategoryTest {

    private WebTarget target;
    private PodamFactory factory = new PodamFactoryImpl();
    private static final String API_PATH = Utils.API_PATH;
    private static final String USERNAME = Utils.USERNAME;
    private static final String PASSWORD = Utils.PASSWORD;

    private static final int OK = Status.OK.getStatusCode();
    private static final int OK_WITHOUT_CONTENT = Status.NO_CONTENT.getStatusCode();

    private static final List<CategoryEntity> oraculo = new ArrayList<>();

    private static  final String ARTIST_PATH = "artists";
    private static  final String ARTWORK_PATH = "artworks";
    private static final String CATEGORY_PATH = "category";

    private ArtistEntity fatherArtistEntity;
    private ArtworkEntity fatherArtworkEntity;

    @ArquillianResource
    private URL deploymentURL;
    
    @PersistenceContext(unitName = "ArtworkPU")
    private EntityManager em;

    @Inject
    private UserTransaction utx;

    @Deployment
    public static WebArchive createDeployment() {
        return ShrinkWrap.create(WebArchive.class)
                // Se agrega las dependencias
                .addAsLibraries(Maven.resolver().loadPomFromFile("pom.xml")
                        .importRuntimeDependencies().resolve()
                        .withTransitivity().asFile())
                // Se agregan los compilados de los paquetes de servicios
                .addPackage(ArtworkResource.class.getPackage())
                // El archivo que contiene la configuracion a la base de datos.
                .addAsResource("META-INF/persistence.xml", "META-INF/persistence.xml")
                // El archivo beans.xml es necesario para injeccion de dependencias.
                .addAsWebInfResource(new File("src/main/webapp/WEB-INF/beans.xml"))
                // El archivo shiro.ini es necesario para injeccion de dependencias
                .addAsWebInfResource(new File("src/main/webapp/WEB-INF/shiro.ini"))
                // El archivo web.xml es necesario para el despliegue de los servlets
                .setWebXML(new File("src/main/webapp/WEB-INF/web.xml"));
    }

    private WebTarget createWebTarget() {
        return ClientBuilder.newClient().target(deploymentURL.toString()).path(API_PATH);
    }

    

    private void clearData() {
        List<ArtworkEntity> records = em.createQuery("SELECT u FROM ArtworkEntity u").getResultList();
        for (ArtworkEntity record : records) {
            em.remove(record);
        }
        em.createQuery("delete from CategoryEntity").executeUpdate();
        em.createQuery("delete from ArtistEntity").executeUpdate();
        oraculo.clear();
    }

   /**
     * Datos iniciales para el correcto funcionamiento de las pruebas.
     *
     * @generated
     */
    private void insertData() {
            fatherArtistEntity = factory.manufacturePojo(ArtistEntity.class);
            em.persist(fatherArtistEntity);
            fatherArtworkEntity = factory.manufacturePojo(ArtworkEntity.class);
            fatherArtworkEntity.setArtist(fatherArtistEntity);
            em.persist(fatherArtworkEntity);

            for (int i = 0; i < 3; i++) {
                CategoryEntity category = factory.manufacturePojo(CategoryEntity.class);
                em.persist(category);
                if(i<2){                
                    fatherArtworkEntity.getCategory().add(category);
                }
                oraculo.add(category);
            }
    }

    /**
     * Configuración inicial de la prueba.
     *
     * @generated
     */
    @Before
    public void setUpTest() {
        try {
            utx.begin();
            clearData();
            insertData();
            utx.commit();            
        } catch (Exception e) {
            Log.getLog().warn(e);
            try {
                utx.rollback();
            } catch (Exception e1) {
                Log.getLog().warn(e1);
            }
        }
        target = createWebTarget()
                .path(ARTIST_PATH)
                .path(fatherArtistEntity.getId().toString())
                .path(ARTWORK_PATH)
                .path(fatherArtworkEntity.getId().toString())
                .path(CATEGORY_PATH);
    }

    /**
     * Login para poder consultar los diferentes servicios
     *
     * @param username Nombre de usuario
     * @param password Clave del usuario
     * @return Cookie con información de la sesión del usuario
     * @generated
     */
    public Cookie login(String username, String password) {
        UserDTO user = new UserDTO();
        user.setUserName(username);
        user.setPassword(password);
        user.setRememberMe(true);
        Response response = createWebTarget()
                .path("users")
                .path("login")
                .request()
                .post(Entity.entity(user, MediaType.APPLICATION_JSON));
        if (response.getStatus() == OK) {
            return response.getCookies().get(JWT.cookieName);
        } else {
            return null;
        }
    }

    /**
     *Prueba para asociar un Category existente a un Artwork
     *
     * @generated
     */
    @Test
    public void addCategoryTest() {
        Cookie cookieSessionId = login(USERNAME, PASSWORD);

        CategoryDTO category = new CategoryDTO(oraculo.get(2));

        Response response = target.path(category.getId().toString())
                .request().cookie(cookieSessionId)
                .post(Entity.entity(category, MediaType.APPLICATION_JSON));

        CategoryDTO categoryTest = (CategoryDTO) response.readEntity(CategoryDTO.class);
        Assert.assertEquals(OK, response.getStatus());
        Assert.assertEquals(category.getId(), categoryTest.getId());
    }

    /**
     * Prueba para obtener una colección de instancias de Category asociadas a una instancia Artwork
     *
     * @generated
     */
    @Test
    public void listCategoryTest() throws IOException {
        Cookie cookieSessionId = login(USERNAME, PASSWORD);

        Response response = target
                .request().cookie(cookieSessionId).get();

        String categoryList = response.readEntity(String.class);
        List<CategoryDTO> categoryListTest = new ObjectMapper().readValue(categoryList, List.class);
        Assert.assertEquals(OK, response.getStatus());
        Assert.assertEquals(2, categoryListTest.size());
    }

    /**
     * Prueba para obtener una instancia de Category asociada a una instancia Artwork
     *
     * @generated
     */
    @Test
    public void getCategoryTest() throws IOException {
        Cookie cookieSessionId = login(USERNAME, PASSWORD);
        CategoryDTO category = new CategoryDTO(oraculo.get(0));

        CategoryDTO categoryTest = target.path(category.getId().toString())
                .request().cookie(cookieSessionId).get(CategoryDTO.class);

        Assert.assertEquals(category.getId(), categoryTest.getId());
        Assert.assertEquals(category.getName(), categoryTest.getName());
    }

    /**
     * Prueba para desasociar un Category existente de un Artwork existente
     *
     * @generated
     */
    @Test
    public void removeCategoryTest() {
        Cookie cookieSessionId = login(USERNAME, PASSWORD);

        CategoryDTO category = new CategoryDTO(oraculo.get(0));

        Response response = target.path(category.getId().toString())
                .request().cookie(cookieSessionId).delete();
        Assert.assertEquals(OK_WITHOUT_CONTENT, response.getStatus());
    }
}
