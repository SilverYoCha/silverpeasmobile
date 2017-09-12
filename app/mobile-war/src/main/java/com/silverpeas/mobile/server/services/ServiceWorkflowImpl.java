/*
 * Copyright (C) 2000 - 2017 Silverpeas
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * As a special exception to the terms and conditions of version 3.0 of
 * the GPL, you may redistribute this Program in connection with Free/Libre
 * Open Source Software ("FLOSS") applications as described in Silverpeas's
 * FLOSS exception.  You should have received a copy of the text describing
 * the FLOSS exception, and it is also available here:
 * "http://www.silverpeas.org/docs/core/legal/floss_exception.html"
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *
 */

package com.silverpeas.mobile.server.services;

import com.silverpeas.mobile.shared.dto.workflow.WorkflowInstanceDTO;
import com.silverpeas.mobile.shared.exceptions.AuthenticationException;
import com.silverpeas.mobile.shared.exceptions.WorkflowException;
import com.silverpeas.mobile.shared.services.ServiceWorkflow;
import org.silverpeas.core.admin.service.OrganizationController;
import org.silverpeas.core.util.ResourceLocator;
import org.silverpeas.core.util.SettingBundle;
import org.silverpeas.core.workflow.api.Workflow;
import org.silverpeas.core.workflow.api.instance.ProcessInstance;
import org.silverpeas.core.workflow.api.user.User;

import java.util.ArrayList;
import java.util.List;

/**
 * Service de gestion des workflows.
 * @author svuillet
 */
public class ServiceWorkflowImpl extends AbstractAuthenticateService implements ServiceWorkflow {

  private static final long serialVersionUID = 1L;
  private OrganizationController organizationController = OrganizationController.get();

  static {
    SettingBundle mobileSettings = ResourceLocator.getSettingBundle("org.silverpeas.mobile.mobileSettings");
  }

  @Override
  public List<WorkflowInstanceDTO> getInstances(String instanceId) throws WorkflowException, AuthenticationException {

    ArrayList<WorkflowInstanceDTO> instances = new ArrayList<WorkflowInstanceDTO>();


    try {

      User user = Workflow.getUserManager().getUser(getUserInSession().getId());
      String[] roles = organizationController.getUserProfiles(getUserInSession().getId(), instanceId);

      ProcessInstance[] processInstances = Workflow.getProcessInstanceManager().getProcessInstances(instanceId, user, roles[0]);

      for (ProcessInstance processInstance : processInstances) {
        WorkflowInstanceDTO dto = new WorkflowInstanceDTO();
        dto.setId(processInstance.getInstanceId());
        String title = processInstance.getTitle(roles[0], getUserInSession().getUserPreferences().getLanguage());
        dto.setTitle(title);
        instances.add(dto);
      }


    } catch (Exception e) {
      throw  new WorkflowException(e);
    }

    return instances;
  }

}
