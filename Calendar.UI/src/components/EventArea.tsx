import { Box, TabPane, Tabs } from "@edgmont-ui/react";
import { EventBox } from "./EventBox";

type EventAreaProps = {
  events: CalendarEvents[];
  pastEvents: CalendarEvents[];
}

export const EventArea: React.FC<EventAreaProps> = ({ events, pastEvents }) => (
  <Box>
    <Tabs value="current">
      <TabPane key="current" value="current" tab="Current Events" p="0">
        <EventBox events={events} />
      </TabPane>

      <TabPane key="past" value="past" tab="Past events" p="0">
        <EventBox events={pastEvents} />
      </TabPane>
    </Tabs>
  </Box>
)