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
  <tr bgcolor="#333333">
    <td colspan="2" align="center"><b><font color="#000000">Glosarios</font></b></td>
   </tr>
  
    <xsl:for-each select="GLOSSARIES/GLOSSARY">
      <tr>
        <td>Nombre del Glosario: <b><xsl:value-of select="./NAME"/></b></td>
        <td>Descripción del Glosario: <xsl:value-of select="./INTRO"/></td>
	  </tr>
	  <tr>
        <td colspan="2" align="center">Términos de Glosario</td>       
	  </tr>
	  
	  <tr>
	   <td colspan="2">
         <table width="100%" border="1" cellspacing="2" cellpadding="2" >
		   <tr bgcolor="#999999">
             <td align="center"><b><font color="#000000">Conceptos</font></b></td>
             <td align="center"><b><font color="#000000">Categoria</font></b></td>
			 <td align="center"><b><font color="#000000">DEFINICIONES</font></b></td>
			</tr>
		
		  <xsl:for-each select="ENTRIES/ENTRY">
			<tr bgcolor="#999999">
             <td><i><font color="#000000"><xsl:value-of select="./CONCEPT"/></font></i></td>
             <td><i><font color="#000000"><xsl:value-of select="./CATEGORIES/CATEGORY/NAME"/></font></i></td>
			 <td>
			   <table width="100%" border="1" cellspacing="2" cellpadding="2" >
			    
				 <tr>
			       <td align="center"><b><font color="#000000">Definición</font></b></td>
                   <td align="center"><b><font color="#000000">Autor</font></b></td>
	               <td align="center"><b><font color="#000000">Nombre Documento</font></b></td>			       
			       <td align="center"><b><font color="#000000">Página</font></b></td>
			       <td align="center"><b><font color="#000000">Localización</font></b></td>
				   <td align="center"><b><font color="#000000">Fecha</font></b></td>
			     </tr>
				 
				 <xsl:for-each select="DEFINITIONS/DEFINITION">
				 <tr>
			       <td><i><font color="#000000"><xsl:value-of select="./INTRO"/></font></i></td>
                   <td><i><font color="#000000"><xsl:value-of select="./AUTOR"/></font></i></td>
	               <td><i><font color="#000000"><xsl:value-of select="./ARCHNAME"/></font></i></td>			       
			       <td><i><font color="#000000"><xsl:value-of select="./PAG"/></font></i></td>
			       <td><i><font color="#000000"><xsl:value-of select="./LOCA"/>\<xsl:value-of select="./ARCH"/></font></i></td>
				   <td><i><font color="#000000"><xsl:value-of select="./FECHA/DIA"/>/<xsl:value-of select="./FECHA/MES"/>/<xsl:value-of select="./FECHA/ANNO"/></font></i></td>
			     </tr>
				</xsl:for-each>
			  </table>
			 </td>
			 
			</tr>
		  </xsl:for-each> 
		 
		
			 <tr>
             <td colspan="3" align="center" bgcolor="#000000"><i><b><font color="#FFFFFF">Fin de Glosario</font></b></i></td>            
			</tr>
		 </table>
	   </td>
	  </tr>
	</xsl:for-each> 
	
	
 </table>

</body>
</html>
</xsl:template>

</xsl:stylesheet>