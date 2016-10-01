<?xml version="1.0" encoding="iso-8859-1"?><!-- DWXMLSource="empleados.xml"  Esto para seleccionar un empleado particular[@id = 1005] --><!DOCTYPE xsl:stylesheet  [
	<!ENTITY nbsp   "&#160;">
	<!ENTITY copy   "&#169;">
	<!ENTITY reg    "&#174;">
	<!ENTITY trade  "&#8482;">
	<!ENTITY mdash  "&#8212;">
	<!ENTITY ldquo  "&#8220;">
	<!ENTITY rdquo  "&#8221;"> 
	<!ENTITY pound  "&#163;">
	<!ENTITY yen    "&#165;">
	<!ENTITY euro   "&#8364;">
]>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
<xsl:output method="html" encoding="iso-8859-1" doctype-public="-//W3C//DTD XHTML 1.0 Transitional//EN" doctype-system="http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd"/>
<xsl:template match="/">

<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1"/>
<title>TERMINOS</title>
</head>

<body>

 <table width="100%" border="1" cellspacing="2" cellpadding="2" >
  <tr bgcolor="#CCCCCC">
    <td><b><font color="#000000">CONCEPTO</font></b></td>
    <td><b><font color="#000000">CATEGORIA</font></b></td>
	<td><b><font color="#000000">DEFINICIONES</font></b></td>
  </tr>
 
    <xsl:for-each select="GLOSSARIES/GLOSSARY/ENTRIES/ENTRY">
     
      <tr>
        <td><xsl:value-of select="CONCEPT"/></td>
        <td><xsl:value-of select="CATEGORIES/CATEGORY/NAME"/></td>
		<td>
		<table width="100%" border="1" cellspacing="2" cellpadding="2" >
		     <tr bgcolor="#CCCCCC">
              <td><b><font color="#000000">Definición</font></b></td>
              <td><b><font color="#000000">Autor</font></b></td>
	          <td><b><font color="#000000">Nombre Documento</font></b></td>
			  <td><b><font color="#000000">Nombre Archivo</font></b></td>
			  <td><b><font color="#000000">Página</font></b></td>
			  <td><b><font color="#000000">Localización</font></b></td>
             </tr>
			  <xsl:for-each select="GLOSSARIES/GLOSSARY/ENTRIES/ENTRY/DEFINITIONS/DEFINITION">
			   <tr bordercolor="#0099CC">
		       <td><xsl:value-of select="INTRO"/></td>  
		       <td><xsl:value-of select="AUTOR"/></td>
			   <td><xsl:value-of select="ARCHNAME"/></td>
		       <td><xsl:value-of select="ARCH"/></td>
			   <td><xsl:value-of select="PAG"/></td>
			   <td><xsl:value-of select="LOCA"/></td>
		     </tr>
			 </xsl:for-each> 
		 </table>
		</td>
      </tr>
	 
    </xsl:for-each>  
 </table>

</body>
</html>

</xsl:template>
</xsl:stylesheet>