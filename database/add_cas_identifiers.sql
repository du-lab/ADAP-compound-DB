INSERT Identifier (SpectrumId, Type, Value) SELECT SpectrumId, 'CAS' AS Type, Value FROM SpectrumProperty WHERE SpectrumProperty.Name = 'CASNO' AND SpectrumProperty.Value != '0'