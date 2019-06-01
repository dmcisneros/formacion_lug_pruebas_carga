<%@ include file="./init.jsp" %>

<%
	List<Asteroid> asteroids = (List<Asteroid>) renderRequest.getAttribute(LUGSSevillaPruebasCargaPortletKeys.ATTR_ASTEROIDS);

%>


<% if(asteroids != null) {
	%>
	
	<h1><liferay-ui:message key="lugs_sevilla_pruebas_carga.asteroids"/></h1>
	<div id="asteroids">
	<%
	Boolean expanded = Boolean.TRUE;
	String expandedClass = "";
	for(Asteroid asteroid : asteroids){
		if(asteroid != null) {
			if(expanded) {
				expandedClass = "expanded";
				expanded = Boolean.FALSE;
			} else {
				expandedClass = "collapsed";
			}
	%>
	
	  <h4 class="header toggler-header-<%=expandedClass%>"><b><%=asteroid.getReadable_des() %></b></h4>
	  <div class="content toggler-content-<%=expandedClass%>">
	  	<ul>
	  		<li><b>RMS: </b><%=asteroid.getRms() %></li>
	  		<li><b>epoch: </b><%=asteroid.getEpoch() %></li>
	  		<li><b>readable_des: </b><%=asteroid.getReadable_des()%></li>
	  		<li><b>H: </b><%=asteroid.getH() %></li>
	  		<li><b>num_obs: </b><%=asteroid.getNum_obs() %></li>
	  		<li><b>ref: </b><%=asteroid.getRef() %></li>
	  		<li><b>G: </b><%=asteroid.getG() %></li>
	  		<li><b>last_obs: </b><%=asteroid.getLast_obs() %></li>
	  		<li><b>comp: </b><%=asteroid.getComp() %></li>
	  		<li><b>M: </b><%=asteroid.getM() %></li>
	  		<li><b>U: </b><%=asteroid.getU() %></li>
	  		<li><b>e: </b><%=asteroid.getE() %></li>
	  		<li><b>a: </b><%=asteroid.getA() %></li>
	  		<li><b>om: </b><%=asteroid.getOm() %></li>
	  		<li><b>pert_p: </b><%=asteroid.getPert_p() %></li>
	  		<li><b>d: </b><%=asteroid.getD() %></li>
	  		<li><b>i: </b><%=asteroid.getI() %></li>
	  		<li><b>des: </b><%=asteroid.getDes() %></li>
	  		<li><b>flags: </b><%=asteroid.getFlags() %></li>
	  		<li><b>num_opp: </b><%=asteroid.getNum_opp() %></li>
	  		<li><b>w: </b><%=asteroid.getW() %></li>
	  		<li><b>pert_c: </b><%=asteroid.getPert_c() %></li>
	  	</ul>
	  </div>
	
	<% }
	  }
	%>
	</div>
	
	<aui:script use="aui-base">
		AUI().use(
		  'aui-toggler',
		  function(Y) {
		    new Y.TogglerDelegate(
		      {
		        animated: true,
		        closeAllOnExpand: true,
		        container: '#asteroids',
		        content: '.content',
		        expanded: false,
		        header: '.header',
		        transition: {
		          duration: 0.2,
		          easing: 'cubic-bezier(0, 0.1, 0, 1)'
		        }
		      }
		    );
		  }
		);
	</aui:script>
<% }%>
