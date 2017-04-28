package com.silverpeas.mobile.server.services;

import com.silverpeas.mobile.server.common.SpMobileLogModule;
import com.silverpeas.mobile.server.services.helpers.FavoritesHelper;
import com.silverpeas.mobile.server.services.helpers.NewsHelper;
import com.silverpeas.mobile.shared.dto.HomePageDTO;
import com.silverpeas.mobile.shared.dto.RightDTO;
import com.silverpeas.mobile.shared.dto.documents.PublicationDTO;
import com.silverpeas.mobile.shared.dto.navigation.ApplicationInstanceDTO;
import com.silverpeas.mobile.shared.dto.navigation.Apps;
import com.silverpeas.mobile.shared.dto.navigation.SilverpeasObjectDTO;
import com.silverpeas.mobile.shared.dto.navigation.SpaceDTO;
import com.silverpeas.mobile.shared.exceptions.AuthenticationException;
import com.silverpeas.mobile.shared.exceptions.HomePageException;
import com.silverpeas.mobile.shared.exceptions.NavigationException;
import com.silverpeas.mobile.shared.services.navigation.ServiceNavigation;
import org.apache.commons.lang3.EnumUtils;
import org.silverpeas.core.admin.component.model.ComponentInstLight;
import org.silverpeas.core.admin.service.Administration;
import org.silverpeas.core.admin.service.OrganizationController;
import org.silverpeas.core.admin.space.SpaceInstLight;
import org.silverpeas.core.contribution.publication.model.PublicationDetail;
import org.silverpeas.core.mylinks.model.LinkDetail;
import org.silverpeas.core.util.SettingBundle;
import org.silverpeas.core.util.logging.SilverLogger;
import org.silverpeas.core.web.look.PublicationHelper;
import org.silverpeas.core.web.util.viewgenerator.html.GraphicElementFactory;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Service de gestion de la navigation dans les espaces et apps.
 * @author svuillet
 */
public class ServiceNavigationImpl extends AbstractAuthenticateService implements ServiceNavigation {

  private static final long serialVersionUID = 1L;
  private OrganizationController organizationController = OrganizationController.get();

  @Override
  public HomePageDTO getHomePageData(String spaceId) throws NavigationException, AuthenticationException {
    checkUserInSession();
    HomePageDTO data = new HomePageDTO();

    List<PublicationDetail> lastNews = NewsHelper.getInstance().getLastNews(getUserInSession().getId(), spaceId);
    data.setNews(NewsHelper.getInstance().populate(lastNews));

    if (spaceId == null || spaceId.isEmpty()) {
      List<LinkDetail> links = FavoritesHelper.getInstance().getBookmarkPersoVisible(getUserInSession().getId());
      data.setFavorites(FavoritesHelper.getInstance().populate(links));
    }
    data.setSpacesAndApps(getSpacesAndApps(spaceId));

    // last publications
    try {
      SimpleDateFormat sdf = new SimpleDateFormat("dd/MM");
      ArrayList<PublicationDTO> lastPubs = new ArrayList<PublicationDTO>();
      SettingBundle settings = GraphicElementFactory.getLookSettings(GraphicElementFactory.defaultLookName);
      int max;
      if (spaceId == null) {
        max = settings.getInteger("home.publications.nb", 3);
      } else {
        max = settings.getInteger("space.homepage.latestpublications.nb", 3);
      }
      List<PublicationDetail> pubs = getPublicationHelper().getPublications(spaceId, max);
      for (PublicationDetail pub : pubs) {
        PublicationDTO dto = new PublicationDTO();
        dto.setId(pub.getId());
        dto.setName(pub.getName());
        dto.setUpdateDate(sdf.format(pub.getUpdateDate()));
        dto.setInstanceId(pub.getInstanceId());
        lastPubs.add(dto);
      }
      data.setLastPublications(lastPubs);

    } catch(Exception e) {e.printStackTrace();}

    return data;
  }

  private boolean isSupportedApp(String appType) {
    return EnumUtils.isValidEnum(Apps.class, appType);
  }

  //TODO : remove appType
  @Override
  public List<SilverpeasObjectDTO> getSpacesAndApps(String rootSpaceId) throws NavigationException, AuthenticationException {
    checkUserInSession();
    ArrayList<SilverpeasObjectDTO> results = new ArrayList<SilverpeasObjectDTO>();
    try {
      if (rootSpaceId == null) {
        String [] spaceIds = Administration.get().getAllSpaceIds(getUserInSession().getId());
        for (String spaceId : spaceIds) {
          SpaceInstLight space = Administration.get().getSpaceInstLightById(spaceId);
          if (space.getFatherId().equals("0")) {
            if (containApp(space)) {
              results.add(populate(space));
            }
          }
        }
        Collections.sort(results);
      } else {
        String [] spaceIds = Administration.get().getAllowedSubSpaceIds(getUserInSession().getId(), rootSpaceId);
        for (String spaceId : spaceIds) {
          SpaceInstLight space = Administration.get().getSpaceInstLightById(spaceId);
          if (("WA"+space.getFatherId()).equals(rootSpaceId)) {
            if (containApp(space)) {
              results.add(populate(space));
            }
          }
        }
        Collections.sort(results);
        ArrayList<SilverpeasObjectDTO> partialResults = new ArrayList<SilverpeasObjectDTO>();
        String [] appsIds = Administration.get().getAvailCompoIds(rootSpaceId, getUserInSession().getId());
        for (String appId : appsIds) {
          ComponentInstLight app = Administration.get().getComponentInstLight(appId);
          if (isSupportedApp(app.getName()) && app.getDomainFatherId().equals(rootSpaceId)) {
            partialResults.add(populate(app));
          }
        }
        Collections.sort(partialResults);
        results.addAll(partialResults);
      }

    } catch (Exception e) {
      SilverLogger.getLogger(SpMobileLogModule.getName()).error("ServiceNavigationImpl.getSpacesAndApps", "root.EX_NO_MESSAGE", e);
    }
    return results;
  }

  @Override
  public ApplicationInstanceDTO getApp(String instanceId) throws NavigationException, AuthenticationException {
    ApplicationInstanceDTO dto = null;
    try {
      ComponentInstLight app = Administration.get().getComponentInstLight(instanceId);
      dto = populate(app);
    } catch(Exception e) {
      SilverLogger.getLogger(SpMobileLogModule.getName()).error("ServiceNavigationImpl.getApp", "root.EX_NO_MESSAGE", e);
    }
    return dto;
  }

  private boolean containApp(SpaceInstLight space) throws Exception {
    String [] appsIds = Administration.get().getAvailCompoIds(space.getId(), getUserInSession().getId());
    for (String appId : appsIds) {
      ComponentInstLight app = Administration.get().getComponentInstLight(appId);
      if (isSupportedApp(app.getName())) {
        return true;
      }
    }
    return false;
  }

  private String[] getUserRoles(String componentId, String userId) {
    return organizationController.getUserProfiles(userId, componentId);
  }

  private SpaceDTO populate(SpaceInstLight space) {
    SpaceDTO dto = new SpaceDTO();
    dto.setId(space.getId());
    dto.setLabel(space.getName());
    dto.setPersonal(space.isPersonalSpace());
    dto.setOrberNum(space.getOrderNum());
    return dto;
  }

  private ApplicationInstanceDTO populate(ComponentInstLight app) {
    ApplicationInstanceDTO dto = new ApplicationInstanceDTO();
    dto.setId(app.getId());
    dto.setLabel(app.getLabel());
    dto.setType(app.getName());

    RightDTO rights = new RightDTO();
    String[] roles = getUserRoles(app.getId(), getUserInSession().getId());
    for (int i = 0; i < roles.length; i++) {
      if (roles[i].equals("admin")) {
        rights.setManager(true);
      }
      if (roles[i].equals("publisher")) {
        rights.setPublisher(true);
      }
      if (roles[i].equals("writer")) {
        rights.setWriter(true);
      }
      if (roles[i].equals("user")) {
        rights.setReader(true);
      }
    }
    dto.setRights(rights);

    try {
      String value = "";
      try {
        value = getMainSessionController().getComponentParameterValue(app.getId(), "notifications");
        dto.setNotifiable(value.equals("yes"));
      } catch(Exception e) {
        dto.setNotifiable(false);
      }
      if (app.getName().equals("kmelia")) {
        value = getMainSessionController().getComponentParameterValue(app.getId(), "tabComments");

      } else if (app.getName().equals("gallery")) {
        value = getMainSessionController().getComponentParameterValue(app.getId(), "comments");
      }
      if (app.getName().equals("kmelia")) {
        dto.setCommentable(value.equals("yes"));
      } else if (app.getName().equals("gallery")) {
        dto.setCommentable(true);
        dto.setNotifiable(true);
      }
    } catch (Exception e) {
      dto.setCommentable(false);
    }

    try {
      String value = "";
      if (app.getName().equals("kmelia")) {
        value = getMainSessionController().getComponentParameterValue(app.getId(), "tabContent");
      }
      dto.setAbleToStoreContent(value.equals("yes"));
    } catch (Exception e) {
      dto.setAbleToStoreContent(false);
    }

    return dto;
  }


  private PublicationHelper getPublicationHelper() throws Exception {
    SettingBundle settings = GraphicElementFactory.getLookSettings(GraphicElementFactory.defaultLookName);
    String helperClassName = settings.getString("publicationHelper", "org.silverpeas.components.kmelia.KmeliaTransversal");
    Class<?> helperClass = Class.forName(helperClassName);
    PublicationHelper kmeliaTransversal = (PublicationHelper) helperClass.newInstance();
    kmeliaTransversal.setMainSessionController(getMainSessionController());

    return kmeliaTransversal;
  }

}