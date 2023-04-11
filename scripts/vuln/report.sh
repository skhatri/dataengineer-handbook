cat vuln.json|jq -r '.Results|.[]|.Vulnerabilities|["Vulnerability ID", "Title", "PkgPath", "PkgName", "Severity", "Installed Version", "Fixed Version"],(.[]?|[.VulnerabilityID,.Title,.PkgPath,.PkgName,.Severity,.InstalledVersion,.FixedVersion])|@csv'

