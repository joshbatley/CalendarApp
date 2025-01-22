import { Box } from "@edgmont-ui/react";
import { Event } from './Event';

type EventBoxProps = {
  events: CalendarEvents[];
}

export const EventBox: React.FC<EventBoxProps> = ({ events }) => (
  <Box border="border.1" mt="4" borderRadius="4" py="4" px="3" bg="offsetBackground" display="grid" gridTemplateColumns="1fr 1fr 1fr" gridGap="10px">
    {events.map((e) => (
      <Event key={e.id} id={e.id} hasPassed={e.hasPassed} end={e.end} summary={e.summary} isActive={e.isActive} start={e.start} />
    ))}
  </Box>
)