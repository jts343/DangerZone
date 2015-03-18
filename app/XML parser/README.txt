DZ_Eboxes.java -- README
2/19/15

Class 'DZ_Eboxes'
	Type 'Ebox' -- can be initialized with lat/lon coordinates
		double latitude -- latitude coordinate component (has set/get methods)
		double longitude -- longitude coordinate component (has set/get methods)
		
	void printToCSV() -- takes an Ebox array as an argument and prints the lat/lon
							coordinates to a .csv file.
	
	Ebox[] parseXML() -- takes an XML file stored as type 'File' as an argument.
							Returns an array of type 'Ebox'
							**Works only with DrexelBoxes.xml**
							
	void main() -- Used for testing purposes.