# Chatt (Real-Time Chat Interface)
> Developed a basic MVP that aligns with the outlined requirements. Given the limited timeframe, the current implementation focuses on functionality over production-readiness.

## Requirements
All key functional requirements were implemented, including:
* Real-time messaging
* WebSocket connectivity
* Tracking of unread messages
* Attempting to retry messages that failed to send
* Basic UI interactions

Except for displaying clear alerts to the user. Instead, the failures were handled gracefully without blocking the user's actions.

## Improvements needed
The following enhancements can be considered:
* Enhance state management approach to handle UI and connection state transitions more cleanly.
* Refactor to follow architecture principles with clear separation between UI, domain, and data layers.
* Implement structured in-memory storage (e.g., repository or caching layer) instead of holding all collections in a single class.
* Optimize handling high volumes of concurrent WebSocket connections to prevent performance bottlenecks
* Securing keys in remote servers (e.g., Firebase Remote Config or Backend)
* UI/UX Refinements
* Code cleanup
