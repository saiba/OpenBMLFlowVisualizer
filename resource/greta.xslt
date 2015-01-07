<?xml version="1.0" encoding="UTF-8"?>
<xsl:transform version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform"> 
<xsl:template match="/bml">
  <bml xmlns="http://www.bml-initiative.org/bml/bml-1.0">
    <xsl:copy-of select="@*|b/@*" />
    <xsl:copy-of select="node()"/>    
  </bml> 	
</xsl:template>
</xsl:transform>
