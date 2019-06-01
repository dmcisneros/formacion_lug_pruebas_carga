
package com.liferay.lugs.sevilla.pruebascarga.renders;

import java.io.Serializable;
import java.util.List;

import javax.portlet.PortletException;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;
import javax.servlet.http.HttpServletRequest;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import com.liferay.lugs.sevilla.pruebascarga.beans.Asteroid;
import com.liferay.lugs.sevilla.pruebascarga.constants.LUGSSevillaPruebasCargaPortletKeys;
import com.liferay.lugs.sevilla.pruebascarga.utils.LUGSSevillaPruebasCargaUtils;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.cache.PortalCache;
import com.liferay.portal.kernel.cache.SingleVMPool;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCRenderCommand;
import com.liferay.portal.kernel.util.PortalUtil;

@Component(immediate = true, property = {
	"javax.portlet.name=" +
		LUGSSevillaPruebasCargaPortletKeys.LUGSSevillaPruebasCarga,
	"mvc.command.name=" +
		LUGSSevillaPruebasCargaPortletKeys.COMMAND_RENDER_DEFAULT
}, service = MVCRenderCommand.class)
public class DefaultMVCRenderCommand implements MVCRenderCommand {

	@Reference
	LUGSSevillaPruebasCargaUtils lugsSevillaPruebasCargaUtils;

	@Reference
	SingleVMPool singleVMPool;

	@Override
	public String render(
		RenderRequest renderRequest, RenderResponse renderResponse)
		throws PortletException {

		HttpServletRequest httpReq = PortalUtil.getOriginalServletRequest(
			PortalUtil.getHttpServletRequest(renderRequest));
		String cache = httpReq.getParameter(
			LUGSSevillaPruebasCargaPortletKeys.PARAM_CACHE);

		PortalCache<Serializable, Serializable> singleVMP =
			(PortalCache<Serializable, Serializable>) singleVMPool.getPortalCache(
				"LUG_SPAIN");

		List<Asteroid> asteroids =
			lugsSevillaPruebasCargaUtils.getAsteroidsDataFromAPI(
				LUGSSevillaPruebasCargaPortletKeys.STRING_TRUE.equalsIgnoreCase(
					cache),
				singleVMP);

		renderRequest.setAttribute(
			LUGSSevillaPruebasCargaPortletKeys.ATTR_ASTEROIDS, asteroids);

		return StringPool.BLANK;
	}

}
