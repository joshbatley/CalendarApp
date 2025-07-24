import { Box, Typography } from "@muffled-ui/react"
import { MapPinIcon } from "@heroicons/react/16/solid";
import { formatDate } from "date-fns"

type EventProps = {
  id: string;
  hasPassed: boolean;
  isActive: boolean;
  summary: string;
  start: string;
  end: string;
}

export const Event: React.FC<EventProps> = ({
  id,
  hasPassed,
  isActive,
  summary,
  start,
  end
}) => (
  <Box p="2" key={id} bg={hasPassed ? 'muted' : 'background'} borderRadius="4" border="border.1">
    <Box display="flex">
      {isActive && (
        <>
          <MapPinIcon width="14px" height="14px" />&nbsp;
        </>
      )}
      {summary}
    </Box>
    <Typography color="mutedForeground" fontSize="0">
      {formatDate(new Date(start), 'do MMMM yyyy')} - {formatDate(new Date(end), 'do MMMM yyyy')}
    </Typography>
  </Box >
);