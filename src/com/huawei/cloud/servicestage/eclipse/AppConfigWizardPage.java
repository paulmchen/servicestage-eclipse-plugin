/**
 * Copyright 2016 - 2018 Huawei Technologies Co., Ltd. All rights reserved.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.huawei.cloud.servicestage.eclipse;

import java.io.IOException;
import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.equinox.security.storage.StorageException;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Spinner;
import org.eclipse.swt.widgets.Text;

/**
 * @author Farhan Arshad
 */
public class AppConfigWizardPage extends AbstractConfigWizardPage
        implements Resources {

    public AppConfigWizardPage(RequestManager requestManger) {
        super(WIZARD_APP_PAGE_PAGE_NAME, requestManger);
        setTitle(WIZARD_APP_PAGE_TITLE);
        setDescription(WIZARD_APP_PAGE_DESCRIPTION);
    }

    @Override
    public void createControl(Composite parent) {
        // outer container
        Composite container = createContainer(parent);
        this.setPageComplete(false);

        //
        // service instance group
        //
        Group serviceInstanceGroup = createGroup(container,
                WIZARD_APP_PAGE_SERVICE_INSTANCE_GROUP_NAME);

        // service instance id is auto generated as a uuid and can not be
        // modified
        addField(ConfigConstants.SERVICE_INSTANCE_ID,
                WIZARD_APP_PAGE_SERVICE_INSTANCE_ID,
                UUID.randomUUID().toString(), false, true,
                serviceInstanceGroup);

        //
        // application group
        //
        Group appGroup = createGroup(container,
                WIZARD_APP_PAGE_APPLICATION_GROUP_NAME);

        // app name
        Text name = addField(ConfigConstants.APP_NAME, WIZARD_APP_PAGE_APP_NAME,
                true, appGroup);

        // restrict app name to alphanumeric
        final Pattern alphanumeric = Pattern.compile("^[a-zA-Z0-9-]*$");
        name.addVerifyListener(e -> {
            String currentName = name.getText();
            String newName = (currentName.substring(0, e.start) + e.text
                    + currentName.substring(e.end));

            Matcher matcher = alphanumeric.matcher(newName);
            e.doit = matcher.matches();
        });

        // display name
        Text displayName = addField(ConfigConstants.APP_DISPLAY_NAME,
                WIZARD_APP_PAGE_APP_DISPLAY_NAME, true, appGroup);

        // description
        Text desc = addField(ConfigConstants.APP_DESCRIPTION,
                WIZARD_APP_PAGE_APP_DESCRIPTION, false, appGroup);

        // version
        Text version = addField(ConfigConstants.APP_VERSION,
                WIZARD_APP_PAGE_APP_VERSION, "1.0", true, true, appGroup);

        // copy app name to display name and description
        // but only if the fields were initally empty, i.e. no saved values
        boolean displayNameInitallyEmpty = displayName.getText().isEmpty();
        boolean descInitallyEmpty = desc.getText().isEmpty();

        name.addModifyListener(event -> {
            if (displayNameInitallyEmpty) {
                displayName.setText(name.getText());
            }

            if (descInitallyEmpty) {
                desc.setText(name.getText());
            }
        });

        // app runtime types
        Map<String, String> types = Collections.emptyMap();
        try {
            types = this.getRequestManger().getApplicationTypes();
        } catch (IOException | StorageException e) {
            Logger.exception(e);
            this.setErrorMessage(WIZARD_APP_PAGE_APP_TYPE_ERROR);
        }

        Combo type = addDropdown(ConfigConstants.APP_TYPE_OPTION,
                WIZARD_APP_PAGE_APP_TYPE, types, true, true, appGroup);

        // port
        Spinner port = addSpinner(ConfigConstants.APP_PORT,
                WIZARD_APP_PAGE_PORT, 8080, 1, 99999, true, appGroup);

        //
        // swr upload info group
        //
        Group localGroup = createGroup(container,
                WIZARD_SRC_PAGE_SWR_GROUP_NAME);

        // only local file is currently supported
        getDialogSettings().put(ConfigConstants.SOURCE_TYPE_OPTION,
                ConfigConstants.SOURCE_TYPE_LOCAL_FILE);

        // repo where binary will be uploaded
        Set<String> repos = Collections.emptySet();
        try {
            repos = this.getRequestManger().getRepos();
        } catch (IOException | StorageException e) {
            Logger.exception(e);
            this.setErrorMessage(e.getMessage());
        }

        Combo repo = addDropdown(ConfigConstants.SWR_REPO,
                WIZARD_SRC_PAGE_SWR_REPO, repos, false, true, localGroup);

        //
        // platform group
        //
        Group platformGroup = createGroup(container,
                WIZARD_APP_PAGE_PLATFORM_GROUP_NAME);

        // CCE clusters
        Map<String, String> cceClusters = Collections.emptyMap();
        try {
            cceClusters = this.getRequestManger().getCCEClusters();
        } catch (IOException | StorageException e) {
            Logger.exception(e);
            this.setErrorMessage(WIZARD_APP_PAGE_APP_CLUSTER_ERROR);
        }

        Combo cce = addDropdown(ConfigConstants.APP_CLUSTER_ID,
                WIZARD_APP_PAGE_APP_CLUSTER, cceClusters, true, true,
                platformGroup);

        // ELBs
        Map<String, String> elbs = Collections.emptyMap();
        try {
            elbs = this.getRequestManger().getELBs();
        } catch (IOException | StorageException e) {
            Logger.exception(e);
            this.setErrorMessage(WIZARD_APP_PAGE_APP_ELB_ERROR);
        }

        Combo elb = addDropdown(ConfigConstants.APP_ELB_ID,
                WIZARD_APP_PAGE_APP_ELB, elbs, true, true, platformGroup);

        // VPCs
        Map<String, String> vpcs = Collections.emptyMap();
        try {
            vpcs = this.getRequestManger().getVPCs();
        } catch (IOException | StorageException e) {
            Logger.exception(e);
            this.setErrorMessage(WIZARD_APP_PAGE_APP_VPC_ERROR);
        }

        Combo vpc = addDropdown(ConfigConstants.APP_VPC_ID,
                WIZARD_APP_PAGE_APP_VPC, vpcs, true, true, platformGroup);

        // app sizes
        Map<String, String> sizes = Collections.emptyMap();
        try {
            sizes = this.getRequestManger().getAppTShirtSizes();
        } catch (IOException | StorageException e) {
            Logger.exception(e);
            this.setErrorMessage(WIZARD_APP_PAGE_APP_SIZE_ERROR);
        }

        Combo size = addDropdown(ConfigConstants.APP_SIZE_OPTION,
                WIZARD_APP_PAGE_APP_SIZE, sizes, true, true, platformGroup);

        // numer of replicas
        Spinner replicas = addSpinner(ConfigConstants.APP_REPLICAS,
                WIZARD_APP_PAGE_APP_REPLICAS, 1, 1, 99, true, platformGroup);

        // this listener checks the mandatory fields and only sets
        // page complete if all mandatory fields are non-empty
        Listener listener = new Listener() {
            @Override
            public void handleEvent(Event arg0) {
                if (Util.isNotEmpty(name.getText())
                        && Util.isNotEmpty(displayName.getText())
                        && Util.isNotEmpty(version.getText())
                        && Util.isNotEmpty(type.getText())
                        && Util.isNotEmpty(port.getText())
                        && Util.isNotEmpty(repo.getText())
                        && Util.isNotEmpty(cce.getText())
                        && Util.isNotEmpty(elb.getText())
                        && Util.isNotEmpty(vpc.getText())
                        && Util.isNotEmpty(size.getText())
                        && Util.isNotEmpty(replicas.getText())) {
                    setPageComplete(true);
                } else {
                    setPageComplete(false);
                }
            }
        };

        // add the above listener to mandatory fields
        name.addListener(SWT.Modify, listener);
        displayName.addListener(SWT.Modify, listener);
        version.addListener(SWT.Modify, listener);
        type.addListener(SWT.Modify, listener);
        port.addListener(SWT.Modify, listener);
        repo.addListener(SWT.Modify, listener);
        cce.addListener(SWT.Modify, listener);
        elb.addListener(SWT.Modify, listener);
        vpc.addListener(SWT.Modify, listener);
        size.addListener(SWT.Modify, listener);
        replicas.addListener(SWT.Modify, listener);

        // little trick to trigger a modify for the first time the page is
        // opened
        name.setText(name.getText());
    }

    @Override
    protected int getPageLabelWidth() {
        return 110;
    }

}