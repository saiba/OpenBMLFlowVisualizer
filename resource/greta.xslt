<?xml version="1.0" encoding="UTF-8"?>
<xsl:transform version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform"> 
<xsl:template match="/bml">
  <bml xmlns="http://www.bml-initiative.org/bml/bml-1.0">
    <xsl:attribute name="id">
      <xsl:value-of select="@id" />
    </xsl:attribute>
    <xsl:attribute name="characterId">
      <xsl:value-of select="@character" />
    </xsl:attribute>
    <xsl:attribute name="composition">
      <xsl:value-of select="@composition" />
    </xsl:attribute>
    <xsl:copy-of select="node()"/>    
  </bml> 	
</xsl:template>
</xsl:transform>
