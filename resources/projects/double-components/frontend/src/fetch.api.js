export async function getHello() {
    const API_URL = "http://backend.lstocchi-dev.svc.apps.sandbox.x8i5.p1.openshiftapps.com:3001";

    const response = await fetch(`${API_URL}/hello`, {
        method: 'GET',
        headers: { 'Content-Type': 'application/json'}
    }).then((response) => {
        if (response.status === 200) {
            return { status: response.status, data: response.json() };
        } else {
            return { status: response.status, data: [] };
        }        
    }).catch((error) => {
        return { status: 500, data: [] }
    });

    return response;
}