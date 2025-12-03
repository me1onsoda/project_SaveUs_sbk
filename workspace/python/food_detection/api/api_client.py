from typing import Optional, List, Dict, Any
from urllib.parse import quote

import httpx


class APIClient:
    timeout: float = 5.0

    def __init__(
            self,
            base_url: str,
            default_params: Optional[Dict[str, Any]] = None,
    ) -> None:
        self.base_url = base_url
        self.default_params = default_params or {}

    def set_path(self, path_params: List[str]) -> None:
        self.base_url = self.base_url.format(
            *[quote(param) for param in path_params]
        )

    async def fetch(
            self,
            extra_params: Optional[Dict[str, Any]] = None,
            client: Optional[httpx.AsyncClient] = None,
    ) -> Dict[str, Any]:
        close_client = False
        data: Dict[str, Any] = {}

        query_params: Dict[str, Any] = {
            **self.default_params,
            **(extra_params or {}),
        }

        if client is None:
            client = httpx.AsyncClient(timeout=self.timeout)
            close_client = True

        try:
            response = await client.get(self.base_url, params=query_params)
            response.raise_for_status()
            data = response.json()
        except Exception as exc:
            print(f"error occurred: {exc}")
        finally:
            if close_client:
                await client.aclose()

        return data

