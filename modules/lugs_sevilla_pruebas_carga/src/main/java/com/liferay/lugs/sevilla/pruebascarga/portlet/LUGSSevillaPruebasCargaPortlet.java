
package com.liferay.lugs.sevilla.pruebascarga.portlet;

import javax.portlet.Portlet;

import org.osgi.service.component.annotations.Component;

import com.liferay.lugs.sevilla.pruebascarga.constants.LUGSSevillaPruebasCargaPortletKeys;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCPortlet;

/**
 * @author LUG Spain Sevilla
 */
@Component(immediate = true, property = {
	"com.liferay.portlet.css-class-wrapper=lugs-sevilla-pruebas-carga",
	"com.liferay.portlet.display-category=LUG Spain",
	"com.liferay.portlet.instanceable=true",
	"com.liferay.portlet.header-portlet-css=/css/main.css",
	"javax.portlet.display-name=LUGS Sevilla Pruebas Carga Portlet",
	"javax.portlet.init-param.template-path=/",
	"javax.portlet.init-param.view-template=/html/view.jsp",
	"javax.portlet.name=" +
		LUGSSevillaPruebasCargaPortletKeys.LUGSSevillaPruebasCarga,
	"javax.portlet.resource-bundle=content.Language",
	"javax.portlet.security-role-ref=power-user,user"
}, service = Portlet.class)
public class LUGSSevillaPruebasCargaPortlet extends MVCPortlet {
}
