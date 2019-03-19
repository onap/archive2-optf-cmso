import json

class JSONUtils:
    """JSONUtils is common resource for simple json helper keywords."""
    
    
    def json_escape(self, jsonObject):
        jsonstr = json.dumps(jsonObject)
        outstr = jsonstr.replace('"', '\\"').replace('\n', '\\n')
        return  outstr    
    
